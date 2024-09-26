package br.com.pentamc.bukkit.event.report;

import br.com.pentamc.bukkit.event.NormalEvent;
import br.com.pentamc.common.report.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportReceiveEvent extends NormalEvent {
	
	private Report report;
	
}
