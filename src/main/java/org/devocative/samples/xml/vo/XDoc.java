package org.devocative.samples.xml.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("doc")
public class XDoc {
	private XPart1 part1;

	private XPart2 part2;

	private List<XPart1> parts;

	public XPart1 getPart1() {
		return part1;
	}

	public void setPart1(XPart1 part1) {
		this.part1 = part1;
	}

	public XPart2 getPart2() {
		return part2;
	}

	public void setPart2(XPart2 part2) {
		this.part2 = part2;
	}

	public List<XPart1> getParts() {
		return parts;
	}

	public void setParts(List<XPart1> parts) {
		this.parts = parts;
	}

	@Override
	public String toString() {
		return "XDoc{" +
			"part1=" + part1 +
			", part2=" + part2 +
			", parts=" + parts +
			'}';
	}
}
