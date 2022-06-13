package se.magnus.api.composite.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(description = "REST API for composite book information")
public interface BookCompositeService {

	/**
     * Sample usage: curl $HOST:$PORT/book-composite/1
     *
     * @param bookId
     * @return the composite book info, if found, else null
     */
	@ApiOperation(
	        value = "${api.book-composite.get-composite-book.description}",
	        notes = "${api.book-composite.get-composite-book.notes}")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
        @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
        @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
        value    = "/book-composite/{bookId}",
        produces = "application/json")
    BookAggregateModel getBook(@PathVariable int bookId);
}
