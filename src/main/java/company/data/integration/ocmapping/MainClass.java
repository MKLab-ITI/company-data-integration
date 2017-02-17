/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package company.data.integration.ocmapping;

import company.data.integration.ocmapping.OCUtils.StateCodes;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] main) {
        StateCodes state_codes = new StateCodes();
        state_codes.findCode("state of columbia");
    }
}
