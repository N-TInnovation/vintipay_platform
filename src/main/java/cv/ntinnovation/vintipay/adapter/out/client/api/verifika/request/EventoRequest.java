package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventoRequest<T> {
    private String tipo;
    private T data;
}
