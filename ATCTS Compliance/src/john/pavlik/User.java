package john.pavlik;

import java.util.Date;

public class User {

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof User) {
			User userObj = (User) obj;
			if ("-".equals(getEdipi()) || "-".equals(userObj.getEdipi())) {
				return (!"".equalsIgnoreCase(getAkoEmail()) && getAkoEmail()
						.equalsIgnoreCase(userObj.getAkoEmail()))
						|| (!"".equalsIgnoreCase(getEnterpriseEmail()) && getEnterpriseEmail()
								.equalsIgnoreCase(userObj.getEnterpriseEmail()));
			} else {
				return getEdipi().equalsIgnoreCase(userObj.getEdipi());
			}
		} else
			return false;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEnterpriseEmail() {
		return enterpriseEmail;
	}

	public String getEdipi() {
		return edipi;
	}

	public Boolean getAup() {
		return aup;
	}

	public Boolean getAwareness() {
		return awareness;
	}

	public Date getLastCyber() {
		return lastCyber;
	}

	/**
	 * @return the aupSigned
	 */
	public Date getAupSigned() {
		return aupSigned;
	}

	/**
	 * @param aupSigned
	 *            the aupSigned to set
	 */
	public void setAupSigned(Date aupSigned) {
		this.aupSigned = aupSigned;
	}

	public String getUnit() {
		return unit;
	}

	private String username;
	private String displayName;
	private String akoEmail;
	private String enterpriseEmail;
	private String edipi;
	private Boolean atc;
	private Boolean aup;
	private Boolean awareness;
	private Date lastCyber;
	private Date aupSigned;
	private String unit;

	public User(String u, String d, String ako, String enterprise, String ed,
			Boolean atc, Boolean aup, Boolean aware, Date lastCyber,
			Date aupSigned, String unit) {
		username = u;
		displayName = d;
		akoEmail = ako;
		enterpriseEmail = enterprise;
		edipi = ed;
		this.atc = atc;
		this.aup = aup;
		awareness = aware;
		this.lastCyber = lastCyber;
		this.aupSigned = aupSigned;
		this.unit = unit;
	}

	public Boolean getAtc() {
		return atc;
	}

	public void setAtc(Boolean atc) {
		this.atc = atc;
	}

	public void setAwareness(Boolean awareness2) {
		awareness = awareness2;
	}

	public void setAUP(Boolean aup2) {
		aup = aup2;
	}

	public void setLastCyber(Date lastCyber2) {
		lastCyber = lastCyber2;
	}

	public void setUnit(String unit2) {
		unit = unit2;
	}

	public String getAkoEmail() {
		return akoEmail;
	}

	public void setAkoEmail(String akoEmail) {
		this.akoEmail = akoEmail;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEnterpriseEmail(String enterpriseEmail) {
		this.enterpriseEmail = enterpriseEmail;
	}

	public void setEdipi(String edipi) {
		this.edipi = edipi;
	}

	public void setAup(Boolean aup) {
		this.aup = aup;
	}

}
