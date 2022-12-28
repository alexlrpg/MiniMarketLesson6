import api.ProductService;
import com.github.javafaker.Faker;
import db.model.Products;
import dto.Product;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import util.RetrofitUtils;
import util.SqlSessionUtils;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class CreateProductTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle("Cherry")
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
    }

    @Test
    void createProductInFoodCategoryTest() throws IOException {
        Response<Product> responce = productService.createProduct(product).execute();
        id = responce.body().getId();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));

        db.dao.ProductsMapper productsMapper = SqlSessionUtils.getSession().getMapper(db.dao.ProductsMapper.class);
        db.model.ProductsExample example = new db.model.ProductsExample();

        example.createCriteria().andTitleLike("Cherry");
        List<Products> list = productsMapper.selectByExample(example);

        assertThat(list.get(0).getTitle(), CoreMatchers.equalTo("Cherry"));
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> responce = productService.deleteProduct(id).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));

        SqlSessionUtils.getSession().close();
    }
}
