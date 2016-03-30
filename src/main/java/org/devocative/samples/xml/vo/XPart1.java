package org.devocative.samples.xml.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("part1")
public class XPart1 {
	@XStreamAsAttribute
	private String p1a;

	@XStreamAsAttribute
	private String p1b;

	public String getP1a() {
		return p1a;
	}

	public void setP1a(String p1a) {
		this.p1a = p1a;
	}

	public String getP1b() {
		return p1b;
	}

	public void setP1b(String p1b) {
		this.p1b = p1b;
	}

	@Override
	public String toString() {
		return "XPart1{" +
			"p1a='" + p1a + '\'' +
			", p1b='" + p1b + '\'' +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XPart1)) return false;

		XPart1 xPart1 = (XPart1) o;

		return !(getP1a() != null ? !getP1a().equals(xPart1.getP1a()) : xPart1.getP1a() != null);

	}

	@Override
	public int hashCode() {
		return getP1a() != null ? getP1a().hashCode() : 0;
	}
}
