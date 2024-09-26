package br.com.pentamc.common.backend.data;

import br.com.pentamc.common.utils.ip.IpInfo;

public interface IpData {

	IpInfo loadIp(String ipAddress);

	void registerIp(IpInfo ipInfo);

	void updateIp(IpInfo ipInfo, String fieldName);

	void deleteIp(String ipAddress);

}
