package org.devocative.samples.xml.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("part2")
public class XPart2 {
	@XStreamAsAttribute
	private String p2y;

	@XStreamAsAttribute
	private String p2z;

	public String getP2y() {
		return p2y;
	}

	public void setP2y(String p2y) {
		this.p2y = p2y;
	}

	public String getP2z() {
		return p2z;
	}

	public void setP2z(String p2z) {
		this.p2z = p2z;
	}

	@Override
	public String toString() {
		return "XPart2{" +
			"p2z='" + p2z + '\'' +
			", p2y='" + p2y + '\'' +
			'}';
	}
}
