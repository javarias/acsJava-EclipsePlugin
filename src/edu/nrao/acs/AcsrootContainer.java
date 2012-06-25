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

public class AcsrootContainer implements IClasspathContainer {

	public final static Path ID = new Path("acs.ACSROOT_CONTAINER");
	public final static String ROOT_DIR = "-";
	
	private IPath path;
	private HashSet<String> ext;
	private File dir;
	private String desc;

	private FilenameFilter dirFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			String[] nameSegs = name.split("[.]");
			// if (nameSegs.length != 2) {
			// return false;
			// }
			// if (nameSegs[0].endsWith("-src")) {
			// return false;
			// }
			if (ext.contains(nameSegs[nameSegs.length - 1].toLowerCase())) {
				return true;
			}

			return false;
		}
	};

	public AcsrootContainer(IPath path, IJavaProject project) {
		this.path = path;
		ext = new HashSet<String>();
		String extString = path.lastSegment();
		String[] extArray = extString.split(",");
		for (String ext : extArray)
			this.ext.add(ext.toLowerCase());

		path = path.removeFirstSegments(1).removeLastSegments(1);
		// File rootProj =
		// project.getProject().getLocation().makeAbsolute().toFile();
		// if (path.segmentCount() == 1 && path.segment(0).equals(ROOT_DIR)){
		// dir = rootProj;
		// path = path.removeFirstSegments(1);
		// } else {
		// dir = new File(rootProj, path.toString());
		// }
		dir = new File(IPath.SEPARATOR + path.toString());

		desc = "ACS Installation " + dir.getAbsolutePath() + " Libraries";
	}
	
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
		File acsrootDir = new File(new File(dir, "ACSSW"), "lib");
		if (acsrootDir.exists() && acsrootDir.isDirectory()) {
			File[] libs = acsrootDir.listFiles(dirFilter);
			for (File lib : libs) {
				// The source are inside the same jars in ACS
				entryList.add(JavaCore.newLibraryEntry(
						new Path(lib.getAbsolutePath()),
						new Path(lib.getAbsolutePath()), new Path("/")));
			}
		}
		// Check is ACScomponent directory exists
		File compDir = new File(acsrootDir, "ACScomponents");
		if (compDir.exists() && compDir.isDirectory()) {
			File[] libs = compDir.listFiles(dirFilter);
			for (File lib : libs) {
				// The source are inside the same jars in ACS
				entryList.add(JavaCore.newLibraryEntry(
						new Path(lib.getAbsolutePath()),
						new Path(lib.getAbsolutePath()), new Path("/")));
			}
		}
		// Check endorsed directory
		File endorsedDir = new File(acsrootDir, "endorsed");
		if (endorsedDir.exists() && endorsedDir.isDirectory()) {
			File[] libs = endorsedDir.listFiles(dirFilter);
			for (File lib : libs) {
				// The source are inside the same jars in ACS
				entryList.add(JavaCore.newLibraryEntry(
						new Path(lib.getAbsolutePath()),
						new Path(lib.getAbsolutePath()), new Path("/")));
			}
		}
		//Check Jacorb jars
		File jacorbDir = new File(new File(dir, "JacORB"), "lib");
		if (jacorbDir.exists() && jacorbDir.isDirectory()) {
			File[] libs = jacorbDir.listFiles(dirFilter);
			for (File lib : libs) {
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
		if (dir.exists() && dir.isDirectory())
			return true;
		return false;
	}
	

}