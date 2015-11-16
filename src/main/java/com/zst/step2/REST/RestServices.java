package com.zst.step2.REST;

import com.zst.step2.SNMP.SNMPManager;
import org.snmp4j.smi.OID;

import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Path("/snmp")
public class RestServices {

    private SNMPManager snmp;

    public RestServices(){
        snmp = new SNMPManager("udp:127.0.0.1/161");
        try {
            snmp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void preDestroy(){
    }

    @Path("getVariable/{oid}")
    @GET
    @Produces("application/json")
    public List<String> getVariable(@PathParam("oid")String oid){
        List<String> response = new LinkedList<String>();
        String value = "";
        try {
            value = snmp.getAsString(new OID(oid));
        } catch (IOException e) {
            response.add("Bad OID");
            return response;
        } catch (NumberFormatException ex){
            response.add("OID is not correct");
            return response;
        }
        System.out.println(value);
        response.add(value);
        return response;
    }

    @Path("/test")
    @GET
    @Produces("application/json")
    public List<String> test(){
        List<String> testList = new ArrayList<String>();
        testList.add("TEST1");
        testList.add("TEST2");
        return testList;
    }



    @Path("getTable/{oid}")
    @GET
    @Produces("application/json")
    public List<List<String>> getTable(@PathParam("oid")String oid){
        List<List<String>> table = new LinkedList<List<String>>();

        try {
            table = snmp.getTable(new OID(oid));
        } catch (NumberFormatException ex){
            System.out.println("OID is not correct");
            table.add(new LinkedList<String>());
            table.get(0).add("OID is not correct");
            return table;
        } catch(IndexOutOfBoundsException ex){
            System.out.println("OID is not a table OID");
            table.add(new LinkedList<String>());
            table.get(0).add("OID is not a table OID");
            return table;
        }
        return table;
    }

}
