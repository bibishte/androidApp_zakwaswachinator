package com.example.tcp_test.socket.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ProgramStruct {
    private String progName;
    private List<OperationParam> program=new ArrayList<OperationParam>();

    public String getProgName() {
        return progName;
    }

    public void setProgName(String progName) {
        this.progName = progName;
    }

    public List<OperationParam> getProgram() {
        return program;
    }

    public void setProgram(List<OperationParam> program) {
        this.program = program;
    }
}
