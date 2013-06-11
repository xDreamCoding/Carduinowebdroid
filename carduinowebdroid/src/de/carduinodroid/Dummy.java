package de.carduinodroid;

import java.util.TimerTask;;

public class Dummy extends TimerTask {

	private TimerTask tt = null;

	public Dummy(TimerTask tt) {
		this.tt = tt;
	}

	@Override
	public void run() {
		tt.run();
	}

}
