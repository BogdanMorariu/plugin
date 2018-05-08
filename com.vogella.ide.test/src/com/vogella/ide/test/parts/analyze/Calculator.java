package com.vogella.ide.test.parts.analyze;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Document;

public class Calculator {
	
	public void analyzeProject(IProject project, List<String> classes, List<String> methods, Integer linesInProject) throws JavaModelException {
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
	
	public int numberOfMethods(IJavaProject project) throws JavaModelException {
		int result = 0;
		for(IPackageFragment pack : project.getPackageFragments())
			result+=numberOfMethods(pack);
		return result;
	}
	
	public int numberOfMethods(IPackageFragment pack) throws JavaModelException {
		int result = 0;
		if(pack.getKind() == IPackageFragmentRoot.K_SOURCE)
			for(ICompilationUnit unit : pack.getCompilationUnits())
				result+=numberOfMethods(unit);
		return result;
	}
	
	public int numberOfMethods(ICompilationUnit unit) throws JavaModelException {
		int result = 0;
		IType[] allTypes = unit.getAllTypes();
		for(IType type : allTypes) {
			IMethod[] methods = type.getMethods();
			result+=methods.length;
		}
		return result;
	}
	
	public int numberOfLines(IJavaProject project) throws JavaModelException {
		int result = 0;
		for(IPackageFragment pack : project.getPackageFragments())
			result+=numberOfLines(pack);
		return result;
	}
	
	public int numberOfLines(IPackageFragment pack) throws JavaModelException {
		int result = 0;
		if(pack.getKind() == IPackageFragmentRoot.K_SOURCE)
			for(ICompilationUnit unit : pack.getCompilationUnits())
				result+=numberOfLines(unit);
		return result;
	}
	
	public int numberOfLines(ICompilationUnit unit) throws JavaModelException {
		int result = 0;
		Document doc = new Document(unit.getSource());
        result+= doc.getNumberOfLines();
		return result;
	}

	public int numberOfLines(Object element) throws JavaModelException {
		if(element instanceof IJavaProject)
			return numberOfLines((IJavaProject)element);
		if(element instanceof IPackageFragment)
			return numberOfLines((IPackageFragment)element);
		if(element instanceof ICompilationUnit)
			return numberOfLines((ICompilationUnit)element);
		return -7;
	}
	
	public int numberOfMethods(Object element) throws JavaModelException {
		if(element instanceof IJavaProject)
			return numberOfMethods((IJavaProject)element);
		if(element instanceof IPackageFragment)
			return numberOfMethods((IPackageFragment)element);
		if(element instanceof ICompilationUnit)
			return numberOfMethods((ICompilationUnit)element);
		return -7;
	}
}
