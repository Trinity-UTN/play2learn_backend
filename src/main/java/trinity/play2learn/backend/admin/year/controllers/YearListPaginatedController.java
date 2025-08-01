package trinity.play2learn.backend.admin.year.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearListPaginatedService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/years/paginated")
@RestController
@AllArgsConstructor
public class YearListPaginatedController {

    private final IYearListPaginatedService yearListService;

    
    /**
     * CU12 - Listar años académicos de forma paginada.
     *
     * @param page Número de página a consultar (base 1).
     * @param pageSize Tamaño de la página.
     * @param orderBy Campo por el cual ordenar los resultados.
     * @param orderType Tipo de ordenación (ascendente o descendente).
     * @param search Texto para buscar en los años académicos.
     * @param filters Lista de filtros a aplicar.
     * @param filtersValues Valores correspondientes a los filtros.
     * @return ResponseEntity con los datos paginados y mensaje de éxito.
     */
    @GetMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<PaginatedData<YearResponseDto>>> listPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(name = "order_type", defaultValue = "asc") String orderType,
            @RequestParam(required = false) String search,
            @RequestParam(name = "filters", required = false) List<String> filters,
            @RequestParam(name = "filtersValues", required = false) List<String> filtersValues
    ) {
        return ResponseFactory.paginated(yearListService.cu12PaginatedListYears(page, pageSize, orderBy, orderType, search, filters, filtersValues),  SuccesfullyMessages.okSuccessfully());
    }
}
