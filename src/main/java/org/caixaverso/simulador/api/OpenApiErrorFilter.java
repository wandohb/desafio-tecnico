package org.caixaverso.simulador.api;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

public class OpenApiErrorFilter implements OASFilter {

    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final String ERROR_SCHEMA_REF = "#/components/schemas/ErrorResponse";

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (openAPI.getPaths() == null) return;

        APIResponse badRequest = criarResposta("Requisicao invalida — campo nulo, valor negativo ou prazo fora de [1, 360]");
        APIResponse notFound = criarResposta("Simulacao nao encontrada para o id informado");

        openAPI.getPaths().getPathItems().forEach((path, pathItem) -> {
            if (pathItem.getPOST() != null) {
                pathItem.getPOST().getResponses().addAPIResponse("400", badRequest);
            }
            if (pathItem.getGET() != null) {
                pathItem.getGET().getResponses().addAPIResponse("404", notFound);
            }
        });
    }

    private APIResponse criarResposta(String descricao) {
        Schema schema = OASFactory.createSchema().ref(ERROR_SCHEMA_REF);
        MediaType mediaType = OASFactory.createMediaType().schema(schema);
        Content content = OASFactory.createContent().addMediaType(MEDIA_TYPE_JSON, mediaType);
        return OASFactory.createAPIResponse().description(descricao).content(content);
    }
}
