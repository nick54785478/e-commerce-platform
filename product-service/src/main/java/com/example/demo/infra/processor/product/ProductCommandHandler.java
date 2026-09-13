package com.example.demo.infra.processor.product;

import com.example.demo.application.command.product.ChangeProductStatusCommand;
import com.example.demo.application.command.product.CreateProductCommand;
import com.example.demo.application.command.product.UpdateProductCommand;
import com.example.demo.application.domain.product.aggregate.Product;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;
import org.springframework.stereotype.Component;

/**
 * Product Command Handler - 商品命令處理器
 * <p>
 * 專責處理對 Product Aggregate 發出的各項命令。
 * 它會透過 Axon Framework 的 @CommandHandler 攔截命令，
 * 並利用 Aggregate Repository 進行狀態載入與行為委派。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ProductCommandHandler {

    /**
     * 注入 Axon 的 Aggregate Repository 以存取或建立 Product。
     */
    private final Repository<Product> productRepository;

    /**
     * CommandHandler - 處理建立商品
     * <p>
     * 透過 Repository 建立一個全新的 Product Aggregate，並初始化其狀態。
     * 成功建立後，會回傳該 Aggregate 的識別碼 (Product ID)。
     * </p>
     *
     * @param command 建立商品命令，包含初始所需資訊
     * @return 產生的商品唯一識別碼
     * @throws Exception 當 Aggregate 建立失敗時拋出
     */
    @CommandHandler
    public String create(CreateProductCommand command) throws Exception {
        return productRepository.newInstance(() -> Product.create(
                command.productId(),
                command.tenantId(),
                command.name(),
                command.description(),
                command.type(),
                command.subType(),
                command.price(),
                command.imageUrls(),
                command.tags()
        )).invoke(Product::getProductId);
    }

    /**
     * CommandHandler - 處理更新商品
     * <p>
     * 透過提供 Product ID 與目前的 Version 載入對應的 Aggregate，
     * 並將資料更新的操作委派給 Aggregate 內部。使用 Version 是為了確保樂觀鎖 (Optimistic Locking) 控制。
     * </p>
     *
     * @param command 更新商品命令，包含欲修改的商品資訊
     */
    @CommandHandler
    public void update(UpdateProductCommand command) {
        productRepository.load(command.productId(), command.version())
                .execute(product -> product.update(command.tenantId(), command.name(), command.description(), command.type(), command.subType(), command.price(), command.imageUrls(), command.tags()));
    }

    /**
     * CommandHandler - 處理商品狀態變更 (如上下架)
     * <p>
     * 載入指定版本的 Product Aggregate，並進行狀態切換。
     * </p>
     *
     * @param command 變更狀態命令，包含目標狀態
     */
    @CommandHandler
    public void changeStatus(ChangeProductStatusCommand command) {
        productRepository.load(command.productId(), command.version())
                .execute(product -> product.changeStatus(command.tenantId(), command.status()));

    }
}
