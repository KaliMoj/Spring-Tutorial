package tasks.bol;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Cannot delete task of an inactive user")  // 400
public class InactiveUserTaskException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
