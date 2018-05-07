package com.vogella.ide.test.parts.Providers;

import java.io.File;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;

public class LineNrLabelProvider extends CellLabelProvider implements IStyledLabelProvider {
	
	private ImageDescriptor image;
	private ResourceManager resourceManager;
	
	public LineNrLabelProvider(ImageDescriptor image) {
		this.image = image;
	}

	@Override
	public StyledString getStyledText(Object element) {
		String nume="class";
		if(element instanceof IPackageFragment) {
            IPackageFragment pack = (IPackageFragment)element;
            StyledString styledString = new StyledString(pack.getElementName());
            return styledString;
        }
        return new StyledString(nume);
	}
	
    @Override
    public Image getImage(Object element) {
        return getResourceManager().createImage(image);
        //return super.getImage(element);
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

    private String getFileName(File file) {
        String name = file.getName();
        return name.isEmpty() ? file.getPath() : name;
    }

	@Override
	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub
		
	}

}