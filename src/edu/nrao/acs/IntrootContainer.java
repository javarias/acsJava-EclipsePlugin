package edu.nrao.acs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import edu.nrao.acs.preference.PreferenceConstants;

public class IntrootContainer implements IClasspathContainer {
	public final static Path ID = new Path("acs.INTROOT_CONTAINER");
	public final static String ROOT_DIR = "-";
	
	private IPath path;
	private HashSet<String> ext;
	private File dir;
	private String desc;
	
	private FilenameFilter dirFilter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			String[] nameSegs = name.split("[.]");
//			if (nameSegs.length != 2) {
//				return false;
//			}
//			if (nameSegs[0].endsWith("-src")) {
//				return false;
//			}
			if (ext.contains(nameSegs[nameSegs.length - 1].toLowerCase())) {
				return true;
			}

			return false;
		}
	};
	
	public IntrootContainer(IPath path, IJavaProject project) {
		this.path = path;
		ext = new HashSet<String>();
		String extString = path.lastSegment();
		String[] extArray = extString.split(",");
		for (String ext: extArray)
			this.ext.add(ext.toLowerCase());
		
		path = path.removeFirstSegments(1).removeLastSegments(1);
		
		String prefValue = 
				AcsJavaPluginActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.INTROOT_LIBS);
		for (String duple: prefValue.split(":")) {
			String[] values = duple.substring(1, duple.length() - 1).split(",");
			if (path.toString().equals(values[0])) {
				dir = new File(values[1]);
				break;
			}
		}
		
		if (dir == null)
			desc = "INTROOT Libs are not present in this filesystem -- " + path;
		else 
			desc = "INTROOT " + dir.getAbsolutePath() + " Libraries";
	}
	
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
		File introotDir = new File(dir, "lib");
		if (introotDir.exists() && introotDir.isDirectory()) {
			File[] libs = introotDir.listFiles(dirFilter);
			for (File lib : libs) {
				// The source are inside the same jars in ACS
				entryList.add(JavaCore.newLibraryEntry(
						new Path(lib.getAbsolutePath()),
						new Path(lib.getAbsolutePath()), new Path("/")));
			}
		}
		// Check is ACScomponent directory exists
		File compDir = new File(introotDir, "ACScomponents");
		if (compDir.exists() && compDir.isDirectory()) {
			File[] libs = compDir.listFiles(dirFilter);
			for (File lib : libs) {
				// The source are inside the same jars in ACS
				entryList.add(JavaCore.newLibraryEntry(
						new Path(lib.getAbsolutePath()),
						new Path(lib.getAbsolutePath()), new Path("/")));
			}
		}
		IClasspathEntry[] entryArray = new IClasspathEntry[entryList.size()];
		return entryList.toArray(entryArray);
	}

	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	@Override
	public IPath getPath() {
		return path;
	}
	
	public boolean isValid() {
		if (dir != null && dir.exists() && dir.isDirectory())
			return true;
		return false;
	}

}
