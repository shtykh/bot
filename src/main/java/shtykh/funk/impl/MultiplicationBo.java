package shtykh.funk.impl;

import shtykh.funk.FunctionBO;

/**
 * Created by shtykh on 01/04/15.
 */
public class MultiplicationBO implements FunctionBO {
	@Override
	public int apply(int a, int b) {
		return a * b;
	}

	@Override
	public String getName() {
		return "Multiplication";
	}
}
