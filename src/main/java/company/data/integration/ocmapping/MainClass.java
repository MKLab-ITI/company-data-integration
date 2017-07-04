/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package company.data.integration.ocmapping;

import company.data.integration.ocmapping.similarityfunctions.CompanyMatchSimilarity;
import company.data.integration.ocmapping.OCUtils.CountryCodes;
import company.data.integration.ocmapping.OCUtils.StateCodes;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] main) throws UnsupportedEncodingException {
        CompanyEntity query = new CompanyEntity.Builder("Coca-Cola Company").build();
        StateCodes state_codes = new StateCodes();
        CountryCodes country_codes = new CountryCodes();
        CompanyMatchSimilarity similarity = new CompanyMatchSimilarity();

        CompanyMapper mapper = new CompanyMapper(
                new OpenCorporatesClient("MCS2yzvPpwycExpJUAFM"),
                state_codes,
                country_codes,
                similarity
        );

        System.out.println(mapper.findMatch(query));

    }
}
