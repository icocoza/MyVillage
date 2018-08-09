package com.ccz.myvillage.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ECmd {
    none("none"), addr_search("addr_search"), gps_search("gps_search"),

    reg_idpw("reg_idpw"), reg_email("reg_email"), reg_phone("reg_phone"), login("login"), signin("signin"),
    change_pw("change_pw"), reissue_email("reissue_email"), reissue_phone("reissue_phone"), verify_email("verify_email"), verify_sms("verify_sms"),
    anony_login("anony_login"), anony_login_buildid("anony_login_buildid"), anony_signin("anony_signin"), find_id("find_id"),

    getcategorylist("getcategorylist"), addboard("addboard"), delboard("delboard"),
    updatetitle("updatetitle"), updatecontent("updatecontent"), updatecategory("updatecategory"), updateboard("updateboard"), boardlist("boardlist"), getcontent("getcontent"),
    like("like"), dislike("dislike"),
    addreply("addreply"), delreply("delreply"), replylist("replylist"),
    addvote("addvote"), selvote("selvote"), voteitemlist("voteitemlist"), voteupdate("voteupdate"), changeselection("changeselection"),
    voteinfolist("voteinfolist"),

    chcreate("chcreate"), chexit("chexit"), chenter("chenter"), chinvite("chinvite"),
    chmime("chmime"), chcount("chcount"), chlastmsg("chlastmsg"), chinfo("chinfo"),

    fileinit("fileinit", true), filesstart("filestart", false), thumbnail("thumbnail", true), uploadfile("uploadfile", false),

    addfriend("addfriend"), delfriend("delfriend"),
    changefriendstatus("changefriendstatus"), friendids("friendids"), friendcnt("friendcnt"), friendinfos("friendinfos"),
    appendme("appendme"), blockme("blockme"), appendmecnt("appendmecnt"), blockmecnt("blockmecnt"),

    geoloc("geoloc"), joinchannel("joinchannel"), leavechannel("leavechannel"),

    msg("msg"), syncmsg("syncmsg"), rcvmsg("rcvmsg"), readmsg("readmsg"), delmsg("delmsg"), online("online"), push("push"),

    all("all");

    private final String value;
    private final boolean needSession;

    private ECmd(String value) {
        this.value = value;
        this.needSession = true;
    }

    private ECmd(String value, boolean needSession) {
        this.value = value;
        this.needSession = needSession;
    }

    public String getValue() {
        return value;
    }

    public static final Map<String, ECmd> StrToAptCmdMap;

    static {
        StrToAptCmdMap = new ConcurrentHashMap<>();
        for(ECmd cmd : ECmd.values())
            StrToAptCmdMap.put(cmd.getValue(), cmd);
    }

    static public ECmd getType(String cmd) {
        ECmd ecmd = StrToAptCmdMap.get(cmd);
        if(ecmd != null)
            return ecmd;
        return none;
    }
}
