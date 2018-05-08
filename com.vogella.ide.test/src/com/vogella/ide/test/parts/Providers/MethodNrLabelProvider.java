package com.vogella.ide.test.parts.Providers;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.vogella.ide.test.parts.analyze.Calculator;

public class MethodNrLabelProvider extends CellLabelProvider implements IStyledLabelProvider {
	
	private ImageDescriptor image;
	private ResourceManager resourceManager;
	private Calculator calculator;
	
	public MethodNrLabelProvider(ImageDescriptor image) {
		this.image = image;
		calculator = new Calculator();
	}

	@Override
	public StyledString getStyledText(Object element) {
	    StyledString styledString = new StyledString("Default_Text");
		try {
			styledString = new StyledString(String.valueOf(calculator.numberOfMethods(element)));
		} catch (JavaModelException e) {
			styledString = new StyledString("NaN");
			e.printStackTrace();
		}
	    return styledString;
	}
	
    @Override
    public Image getImage(Object element) {
        return getResourceManager().createImage(image);
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
