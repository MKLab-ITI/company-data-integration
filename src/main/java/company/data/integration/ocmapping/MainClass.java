/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package company.data.integration.ocmapping;

import company.data.integration.ocmapping.OCUtils.StateCodes;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] main) throws UnsupportedEncodingException {
        CompanyQuery query = new CompanyQuery.Builder("Nike Inc").build();
        CompanyMapper mapper = new CompanyMapper(new OpenCorporatesClient("********"));
        
        System.out.println(mapper.findMatch(query));
        
    }
}
