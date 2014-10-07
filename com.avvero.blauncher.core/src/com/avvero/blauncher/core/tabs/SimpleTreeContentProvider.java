package com.avvero.blauncher.core.tabs;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class SimpleTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {			// 
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}