package shtykh.funk.impl;

import shtykh.funk.FunctionBO;

public class SummBO implements FunctionBO {
	@Override
	public int apply(int a, int b) {
		return a + b;
	}

	@Override
	public String getName() {
		return "Summ";
	}
}