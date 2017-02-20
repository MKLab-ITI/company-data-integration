/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package company.data.integration.ocmapping;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] main) throws UnsupportedEncodingException {
        CompanyQuery query = new CompanyQuery.Builder("Coca-Cola Company").build();
        CompanyMapper mapper = new CompanyMapper(new OpenCorporatesClient("**********"));
        
        System.out.println(mapper.findMatch(query));
        
    }
}
