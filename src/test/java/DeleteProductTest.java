import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import util.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class DeleteProductTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @SneakyThrows
    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));

        Response<Product> responce = productService.createProduct(product).execute();
        id = responce.body().getId();
    }

    @Test
    void deleteProductTest() throws IOException {
        Response<ResponseBody> responce = productService.deleteProduct(id).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));
    }
}
