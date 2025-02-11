package br.com.pentamc.common.utils.ip;

import br.com.pentamc.common.CommonGeneral;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IpInfo {

	private String status;

	private String country;
	private String countryCode;

	private String regionName;
	private String city;

	private String query;

	private IpStatus ipStatus = IpStatus.ACCEPT;

	public String getIpAddress() {
		return query;
	}

	public void check() {
		if (status.equals("success")) {
			if (country.equalsIgnoreCase("brazil") || country.equalsIgnoreCase("portugal")
					|| country.equalsIgnoreCase("angola") || regionName.equalsIgnoreCase("florida"))
				return;

			ipStatus = IpStatus.NOT_ALLOWED;
		}
	}

	public void setIpStatus(IpStatus ipStatus) {
		if (this.ipStatus == ipStatus)
			return;

		this.ipStatus = ipStatus;
		CommonGeneral.getInstance().getIpData().updateIp(this, "ipStatus");
	}

	public enum IpStatus {

		BLOCKED, NOT_ALLOWED, ACCEPT;

	}

}
