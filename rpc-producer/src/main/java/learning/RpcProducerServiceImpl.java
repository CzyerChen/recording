package learning;

import java.util.UUID;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 15:07
 */
public class RpcProducerServiceImpl  implements RpcProducerService{
    @Override
    public Person getPerson() {
        return new Person(UUID.randomUUID().toString(),"czy");
    }
}
