package com.mybank.pc.admin.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.mybank.pc.core.CoreModel;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseParam<M extends BaseParam<M>> extends CoreModel<M> implements IBean {

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}

	public java.math.BigInteger getId() {
		return get("id");
	}

	public void setK(java.lang.String k) {
		set("k", k);
	}

	public java.lang.String getK() {
		return getStr("k");
	}

	public void setVal(java.lang.String val) {
		set("val", val);
	}

	public java.lang.String getVal() {
		return getStr("val");
	}

	public void setNote(java.lang.String note) {
		set("note", note);
	}

	public java.lang.String getNote() {
		return getStr("note");
	}

}
