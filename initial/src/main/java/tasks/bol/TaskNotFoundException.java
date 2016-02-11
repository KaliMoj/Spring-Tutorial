package tasks.bol;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Task")  // 404
public class TaskNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
