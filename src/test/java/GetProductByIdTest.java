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

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetProductByIdTest {

    static ProductService productService;
    Product product;
    Faker faker = new Faker();
    int id;
    long idl;

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
        idl = id;
    }

    @SneakyThrows
    @Test
    void getProductByIdTest() {
        Response<Product> responce = productService.getProductById(id).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));
        assertThat(responce.code(), equalTo(200));

        db.dao.ProductsMapper productsMapper = SqlSessionUtils.getSession().getMapper(db.dao.ProductsMapper.class);
        db.model.ProductsExample example = new db.model.ProductsExample();

        example.createCriteria().andIdEqualTo(idl);
        List<Products> list = productsMapper.selectByExample(example);

        assertThat(productsMapper.countByExample(example), equalTo(1L));
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> responce = productService.deleteProduct(id).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));

        SqlSessionUtils.getSession().close();
    }
}
