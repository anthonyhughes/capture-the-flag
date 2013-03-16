package com.anthony.ctf.game;

/**
 * Properties of game set here.
 * TODO - Data should be persisted and available via MarkLogic.
 * @author anthonyhughes
 */
public class Player {
	
	private boolean flag = false;

	public boolean possessFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
