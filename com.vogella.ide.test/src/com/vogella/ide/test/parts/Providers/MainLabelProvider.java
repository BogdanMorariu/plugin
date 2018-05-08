package com.vogella.ide.test.parts.Providers;

import java.io.File;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class MainLabelProvider extends CellLabelProvider implements IStyledLabelProvider {
	
	private ImageDescriptor projectImage;
	private ImageDescriptor packageImage;
	private ImageDescriptor classImage;
	private ResourceManager resourceManager;
	
	public MainLabelProvider(ImageDescriptor projectImage,ImageDescriptor packageImage,ImageDescriptor classImage) {
		this.projectImage = projectImage;
		this.packageImage = packageImage;
		this.classImage = classImage;
	}

	@Override
	public StyledString getStyledText(Object element) {
		String nume="class";
		if(element instanceof IPackageFragment) {
            IPackageFragment pack = (IPackageFragment)element;
            StyledString styledString = new StyledString(pack.toString().split(" ")[0]);
            return styledString;
        }else if(element instanceof ICompilationUnit) {
        	ICompilationUnit unit = (ICompilationUnit)element;
        	StyledString styledString = new StyledString(trimName(unit.toString()));
            return styledString;
        }
        return new StyledString(nume);
	}
	
	private String trimName(String name) {
		String result = "";
		int brackets = 0;
		for(int i=0;i<name.length();i++) {
			if(name.charAt(i)=='[')
				brackets++;
			if(brackets==0)
				result+=name.charAt(i);
			if(name.charAt(i)==']')
				brackets--;
		}
		return result;
	}
	
    @Override
    public Image getImage(Object element) {
    	Image result = null;
    	if(element instanceof IJavaProject)
    		result = getResourceManager().createImage(projectImage);
    	if(element instanceof IPackageFragment)
    		result = getResourceManager().createImage(packageImage);
    	if(element instanceof ICompilationUnit)
    		result = getResourceManager().createImage(classImage);
        return result;
    }

    @Override
    public void dispose() {
        // garbage collect system resources
        if(resourceManager != null) {
            resourceManager.dispose();
            resourceManager = null;
        }
    }

    protected ResourceManager getResourceManager() {
        if(resourceManager == null) {
            resourceManager = new LocalResourceManager(JFaceResources.getResources());
        }
        return resourceManager;
    }

	@Override
	public void update(ViewerCell cell) {
		
	}

}
