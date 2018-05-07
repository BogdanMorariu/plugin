package com.vogella.ide.test.parts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.Document;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.vogella.ide.test.parts.Providers.MainLabelProvider;
import com.vogella.ide.test.parts.Providers.MyTreeContentProvider;

public class PartEclipse4x {
	private ResourceManager resourceManager;
	private TreeViewer viewer;

	@PostConstruct
	public void createPartControl(Composite parent) throws CoreException {
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new MyTreeContentProvider());
		//viewer.setLabelProvider(new MyLabelProvider(createImageDescriptor()));
		//System.out.println("whaaaaat??????");
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);

		// Column setup
		TreeViewerColumn mainColumn = new TreeViewerColumn(viewer, SWT.None);
		mainColumn.getColumn().setWidth(300);
		mainColumn.getColumn().setText("Classes");
		mainColumn.setLabelProvider(
                new DelegatingStyledCellLabelProvider(
                        new MainLabelProvider(createImageDescriptor())));
		
		TreeViewerColumn metricsColumn = new TreeViewerColumn(viewer, SWT.None);
		metricsColumn.getColumn().setWidth(300);
		metricsColumn.getColumn().setText("Nr. of Lines");
		metricsColumn.setLabelProvider(new MainLabelProvider(createImageDescriptor()));
		

		// Font
		viewer.getTree().setFont(resourceManager.createFont(FontDescriptor.createFrom("Arial", 12, SWT.ITALIC)));

		// On-Click
		Tree tree = (Tree) viewer.getControl();
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				if (item.getItemCount() > 0) {
					item.setExpanded(!item.getExpanded());
					viewer.refresh();
				}
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer view = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
				Object selectedNode = thisSelection.getFirstElement();
				view.setExpandedState(selectedNode, !view.getExpandedState(selectedNode));
			}
		});

		// Populate
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<String> resourceNames = new ArrayList<>();

		for (IProject project : projects) {
			if (project.isOpen()) {
				processContainer(project, resourceNames);
				IJavaProject javaProject = JavaCore.create(project);
				List<IPackageFragment> packages = new ArrayList<>();
				for(IPackageFragment pack : javaProject.getPackageFragments())
					if (pack.getKind() == IPackageFragmentRoot.K_SOURCE) {
						packages.add(pack);
					}
				viewer.setInput(packages.toArray());
			}
		}
		
		List<String> classes = new ArrayList<String>();
		List<String> methods = new ArrayList<String>();
		Integer linesInProject = 0;
		for (IProject project : projects) {
			if (project.isOpen()) {
				analyzeProject(project, classes, methods, linesInProject);
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"));
			StringBuffer s = new StringBuffer();
			s.append("Number of classes: ").append(classes.size()).append("\n");
			s.append("Number of methods: ").append(methods.size()).append("\n");
			s.append("Total number of lines: ").append(linesInProject);
			writer.write(s.toString());
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//viewer.setInput(resourceNames.toArray());
	
		// viewer.setInput(File.listRoots());
		// ClassLoader cl = ClassLoader.getSystemClassLoader();
		// viewer.setInput(cl.getDefinedPackages());
		GridLayoutFactory.fillDefaults().generateLayout(parent);
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private ImageDescriptor createImageDescriptor() {
		Bundle bundle = FrameworkUtil.getBundle(MainLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/package.png"), null);
		return ImageDescriptor.createFromURL(url);
	}

	void processContainer(IContainer container, List<String> resourceNames) throws CoreException {
		IResource[] members = container.members();

		for (IResource resource : members) {
			if (resource instanceof IContainer) {
				processContainer((IContainer) resource, resourceNames);
			} else if (resource instanceof IFile) {
				String ext = resource.getFileExtension();
				if (ext.equals("java"))
					resourceNames.add(resource.getName()); //+ "-" + resource.getFullPath().toString());
			}
		}
	}
	
	void analyzeProject(IProject project, List<String> classes, List<String> methods, Integer linesInProject) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragment[] packages = javaProject.getPackageFragments();
        for (IPackageFragment mypackage : packages) {
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
            	for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
                    String clas = unit.getElementName();
                    Document doc = new Document(unit.getSource());
                    linesInProject += doc.getNumberOfLines();
                    clas += "(" + doc.getNumberOfLines() + ")";
                    classes.add(clas);
                    IType[] allTypes = unit.getAllTypes();
                    for (IType type : allTypes) {
                    	IMethod[] methodz = type.getMethods();
                        for (IMethod method : methodz) {
                        	methods.add(method.getElementName());
                        }
                    } 
                }
            }
        }
	}
}
