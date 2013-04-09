package edu.nrao.acs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class IntlistContainer implements IClasspathContainer {
	public final static Path ID = new Path("acs.INTLIST_CONTAINER");
	public final static String ROOT_DIR = "-";
	
	private IPath path;
	private HashSet<String> ext;
	private File dir;
	private String desc;
	private File orderFile;
	
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
	
	public IntlistContainer(IPath path, IJavaProject project){
		this.path = path;
		ext = new HashSet<String>();
		String extString = path.lastSegment();
		String[] extArray = extString.split(",");
		for (String ext: extArray)
			this.ext.add(ext.toLowerCase());
		
		path = path.removeFirstSegments(1).removeLastSegments(1);
//		File rootProj = project.getProject().getLocation().makeAbsolute().toFile();
//		if (path.segmentCount() == 1 && path.segment(0).equals(ROOT_DIR)){
//			dir = rootProj;
//			path = path.removeFirstSegments(1);
//		} else {
//			dir = new File(rootProj, path.toString());
//		}
		
		dir = new File(IPath.SEPARATOR + path.toString());
		
		orderFile = new File(dir, "intlist.order");
		desc = "INTLIST " + dir.getAbsolutePath() + " Libraries";
	}

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		ArrayDeque<IClasspathEntry> entryList = new ArrayDeque<IClasspathEntry>();
		FileInputStream fstream;
		DataInputStream in = null;
		BufferedReader br;
		try {
			fstream = new FileInputStream(orderFile);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				//Check for directories declared in intlist.order 
				File intlistDir = new File(dir.getAbsolutePath() + IPath.SEPARATOR
						+ strLine + IPath.SEPARATOR + "lib");
				if (intlistDir.exists() && intlistDir.isDirectory()) {
					File[] libs = intlistDir.listFiles(dirFilter);
					for (File lib : libs) {
						// The source are inside the same jars in ACS
						entryList.addFirst(JavaCore.newLibraryEntry(
										new Path(lib.getAbsolutePath()),
										new Path(lib.getAbsolutePath()),
										new Path("/")));
					}
				}
				//Check is ACScomponent directory exists
				File compDir = new File(intlistDir, "ACScomponents");
				if (compDir.exists() && compDir.isDirectory()) {
					File[] libs = compDir.listFiles(dirFilter);
					for (File lib : libs) {
						// The source are inside the same jars in ACS
						entryList.add(JavaCore.newLibraryEntry(
										new Path(lib.getAbsolutePath()),
										new Path(lib.getAbsolutePath()),
										new Path("/")));
					}
				}
			}
		} catch (FileNotFoundException e) {
			// The code should not reach this point
			Logger.log(Logger.ERROR, e);
		} catch (IOException e) {
			Logger.log(Logger.ERROR, e);
		}
		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				Logger.log(Logger.WARNING, e);
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
		if (dir.exists() && dir.isDirectory() && existOrderFile())
			return true;
		return false;
	}
	
	private boolean existOrderFile() {
		if (orderFile.exists() && !orderFile.isDirectory() && orderFile.canRead())
			return true;
		return false;
	}

}
