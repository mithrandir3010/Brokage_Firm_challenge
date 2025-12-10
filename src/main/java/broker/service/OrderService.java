package broker.service;

import broker.exception.*;
import broker.model.Asset;
import broker.model.Order;
import broker.model.OrderSide;
import broker.model.OrderStatus;
import broker.repository.AssetRepository;
import broker.repository.CustomerRepository;
import broker.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final CustomerRepository customerRepository;

    private static final String TRY_ASSET = "TRY";

    @Override
    @Transactional
    public Order createOrder(Long customerId, String assetName, OrderSide orderSide, BigDecimal size, BigDecimal price) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        if (orderSide == OrderSide.BUY) {
            Asset tryAsset = assetRepository.findAssetForUpdate(customerId, TRY_ASSET);
            BigDecimal requiredAmount = size.multiply(price);
            
            if (tryAsset == null || tryAsset.getUsableSize().compareTo(requiredAmount) < 0) {
                BigDecimal available = tryAsset != null ? tryAsset.getUsableSize() : BigDecimal.ZERO;
                throw new InsufficientBalanceException(TRY_ASSET, requiredAmount, available);
            }
            
            tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(requiredAmount));
            assetRepository.save(tryAsset);
            
        } else if (orderSide == OrderSide.SELL) {
            Asset asset = assetRepository.findAssetForUpdate(customerId, assetName);
            
            if (asset == null || asset.getUsableSize().compareTo(size) < 0) {
                BigDecimal available = asset != null ? asset.getUsableSize() : BigDecimal.ZERO;
                throw new InsufficientBalanceException(assetName, size, available);
            }
            
            asset.setUsableSize(asset.getUsableSize().subtract(size));
            assetRepository.save(asset);
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(orderSide);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
        }
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> listOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> listPendingOrders(Long customerId) {
        return orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Bu emri iptal etme yetkiniz yok");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderNotCancellableException(orderId, order.getStatus());
        }

        if (order.getOrderSide() == OrderSide.BUY) {
            Asset tryAsset = assetRepository.findAssetForUpdate(customerId, TRY_ASSET);
            BigDecimal refundAmount = order.getSize().multiply(order.getPrice());
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(refundAmount));
            assetRepository.save(tryAsset);
        } else {
            Asset asset = assetRepository.findAssetForUpdate(customerId, order.getAssetName());
            asset.setUsableSize(asset.getUsableSize().add(order.getSize()));
            assetRepository.save(asset);
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order matchOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderNotMatchableException(orderId, order.getStatus());
        }

        Long customerId = order.getCustomerId();
        BigDecimal amount = order.getSize().multiply(order.getPrice());

        if (order.getOrderSide() == OrderSide.BUY) {
            Asset tryAsset = assetRepository.findAssetForUpdate(customerId, TRY_ASSET);
            tryAsset.setSize(tryAsset.getSize().subtract(amount));
            assetRepository.save(tryAsset);

            Asset asset = assetRepository.findAssetForUpdate(customerId, order.getAssetName());
            if (asset == null) {
                asset = new Asset();
                asset.setCustomerId(customerId);
                asset.setAssetName(order.getAssetName());
                asset.setSize(order.getSize());
                asset.setUsableSize(order.getSize());
            } else {
                asset.setSize(asset.getSize().add(order.getSize()));
                asset.setUsableSize(asset.getUsableSize().add(order.getSize()));
            }
            assetRepository.save(asset);

        } else {
            Asset asset = assetRepository.findAssetForUpdate(customerId, order.getAssetName());
            asset.setSize(asset.getSize().subtract(order.getSize()));
            assetRepository.save(asset);

            Asset tryAsset = assetRepository.findAssetForUpdate(customerId, TRY_ASSET);
            if (tryAsset == null) {
                tryAsset = new Asset();
                tryAsset.setCustomerId(customerId);
                tryAsset.setAssetName(TRY_ASSET);
                tryAsset.setSize(amount);
                tryAsset.setUsableSize(amount);
            } else {
                tryAsset.setSize(tryAsset.getSize().add(amount));
                tryAsset.setUsableSize(tryAsset.getUsableSize().add(amount));
            }
            assetRepository.save(tryAsset);
        }

        order.setStatus(OrderStatus.MATCHED);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
