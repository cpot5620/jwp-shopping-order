package cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handlerAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler({CartItemException.IllegalMember.class, OrderException.IllegalMember.class})
    public ResponseEntity<Void> handleForbiddenNException(CartItemException.IllegalMember e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler({
            ProductException.NotFoundProduct.class,
            CartItemException.NotFound.class,
            MemberException.NotFound.class,
            OrderException.NotFound.class,
            PointException.NotFound.class,
            PointHistoryException.NotFound.class
    })
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            PointException.BadRequest.class,
    })
    public ResponseEntity<Void> handleBadRequestException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(UnKnownException.class)
    public ResponseEntity<Void> handleUnknownException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
