package ru.sergdm.wsdb.model;

import ru.sergdm.wsdb.client.model.Account;

public class CreateUserResponse {
	private User user;
	private Account account;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "createUserResponse{" +
				"user=" + user +
				", account=" + account +
				'}';
	}
}
