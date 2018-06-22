package company.data.integration.ocmapping;

import company.data.integration.ocmapping.similarityfunctions.CompanyMatchSimilarity;
import company.data.integration.ocmapping.OCUtils.CountryCodes;
import company.data.integration.ocmapping.OCUtils.Jurisdictions;
import company.data.integration.ocmapping.OCUtils.StateCodes;
import java.io.UnsupportedEncodingException;
import javafx.util.Pair;
import org.bson.Document;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] main) throws UnsupportedEncodingException {
        CompanyMatchSimilarity similarity = new CompanyMatchSimilarity();
        CompanyEntity query = new CompanyEntity.Builder("Apple Inc").build();

        CompanyMapper mapper = new CompanyMapper(
                new OpenCorporatesClient("*****"),
                new Jurisdictions(),
                similarity
        );

        Document result = mapper.findMatch(query);

        System.out.println(result.toJson());
    }
}
