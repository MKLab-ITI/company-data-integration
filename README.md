# company-data-integration
Implements techniques for matching between company-related data across different sources.

<h3>Data Integration with OpenCorporates</h3>
`CompanyMapper` is responsible for mapping a given company with a legal entity as defined by OpenCorporates. Users can give as input the company name, country and state (if headquarters are in the United States). If only company name is available `CompanyMapper` search for the given company in Wikipedia to obtain more information. OpenCorporates API is queried and returns a number of resutls. Then, a company is selected as a match based on a number of criteria.

          CompanyMapper mapper = new CompanyMapper(
                                        new OpenCorporatesClient(api_token),
                                        new Jurisdictions(),
                                        new CompanyMatchSimilarity()
                                );
          CompanyQuery query = new CompanyQuery.Builder("Coca-Cola Company").build();
          String oc_company_number = mapper.find(query);
