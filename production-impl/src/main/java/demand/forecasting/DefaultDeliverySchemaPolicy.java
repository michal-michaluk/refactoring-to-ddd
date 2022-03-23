package demand.forecasting;

import enums.DeliverySchema;

public class DefaultDeliverySchemaPolicy {

    /**
     * <pre>
     * Rules for Default Delivery Schema:
     * If delivery scheme is not defined in callof document, we apply following rules:
     *  Product refNo: 461952387712 and 461952816051 are delivered every 3 hours during day shifts every day.
     *   All products for customer „Pralki Wir” (refNo starts with 51) need to be delivered till end of production day.
     *  There is no need to produce other products just in time so we can treat them as delivery at start of production day.
     * </pre>
     */
    public static DeliverySchema defaultFor(String productRefNo) {
        if (productRefNo.equals("461952387712") || productRefNo.equals("461952816051")) {
            return DeliverySchema.every3hours;
        }
        if (productRefNo.startsWith("51")) {
            return DeliverySchema.tillEndOfDay;
        }
        return DeliverySchema.atDayStart;
    }

}
