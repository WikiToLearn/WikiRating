/**
 * 
 */
package org.wikitolearn.wikirating.model;

import java.util.List;

/**
 * @author aletundo
 *
 */
public class CourseTree {
	private String root;
	private List<String> levelsTwo;
	private List<List<String>> levelsTree;
	
	public CourseTree(){}
	
	/**
	 * @param root
	 * @param levelsTwo
	 * @param levelsTree
	 */
	public CourseTree(String root, List<String> levelsTwo, List<List<String>> levelsTree) {
		this.root = root;
		this.levelsTwo = levelsTwo;
		this.levelsTree = levelsTree;
	}

	/**
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * @return the levelsTwo
	 */
	public List<String> getLevelsTwo() {
		return levelsTwo;
	}

	/**
	 * @param levelsTwo the levelsTwo to set
	 */
	public void setLevelsTwo(List<String> levelsTwo) {
		this.levelsTwo = levelsTwo;
	}

	/**
	 * @return the levelsTree
	 */
	public List<List<String>> getLevelsTree() {
		return levelsTree;
	}

	/**
	 * @param levelsTree the levelsTree to set
	 */
	public void setLevelsTree(List<List<String>> levelsTree) {
		this.levelsTree = levelsTree;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CourseTree [root=" + root + ", levelsTwo=" + levelsTwo + ", levelsTree=" + levelsTree + "]";
	}
}
