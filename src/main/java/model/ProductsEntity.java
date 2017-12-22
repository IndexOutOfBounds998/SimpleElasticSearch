package model;

import annotations.Document;

import annotations.Document;
import annotations.Field;
import annotations.ID;
import data.FieldIndex;
import data.FieldType;

/**
 * Created by yang on 2017/7/12.
 */
@Document(index = "shanhu", type = "products", replicas = 1, shards = 5, settings = "Setting.json")
public class ProductsEntity
{
    @ID
    private Integer productId;
    
    @Field(type = FieldType.Integer, index = FieldIndex.not_analyzed)
    private Integer brandId;
    
    @Field(type = FieldType.Integer, index = FieldIndex.not_analyzed)
    private Integer userId;
    
    @Field(type = FieldType.Integer, index = FieldIndex.not_analyzed)
    private Integer classificationId;
    
    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer systemType;
    
    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer sellStatus;
    
    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer isHidePrice;
    
    @Field(type = FieldType.date)
    private String createTime;
    
    @Field(type = FieldType.date)
    private String updataTime;
    
    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer isRecommend;
    
    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer sort;
    
    @Field(type = FieldType.text, index = FieldIndex.analyzed)
    private String productOrgPrice;
    
    @Field(type = FieldType.text, index = FieldIndex.analyzed)
    private String productWatermarkUrl;
    
    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer isDelete;
    
    @Field(type = FieldType.keyword, fields = true, boost = "10")
    private String productName;
    
    @Field(type = FieldType.keyword, fields = true)
    private String classificationName;
    
    @Field(type = FieldType.keyword, fields = true)
    private String brandCnname;
    
    @Field(type = FieldType.keyword, fields = true)
    private String brandName;
    
    @Field(type = FieldType.text)
    private String productImg;
    
    @Field(type = FieldType.text)
    private String productPrice;
    
    @Field(type = FieldType.keyword, fields = true)
    private String productMaterial;
    
    @Field(type = FieldType.text)
    private String productCode;
    
    @Field(type = FieldType.keyword, fields = true)
    private String notice;
    
    @Field(type = FieldType.keyword, fields = true)
    private String productIntro;
    
    public String getProductWatermarkUrl()
    {
        return productWatermarkUrl;
    }
    
    public void setProductWatermarkUrl(String productWatermarkUrl)
    {
        this.productWatermarkUrl = productWatermarkUrl;
    }
    
    public Integer getSort()
    {
        return sort;
    }
    
    public void setSort(Integer sort)
    {
        this.sort = sort;
    }
    
    public String getProductImg()
    {
        return productImg;
    }
    
    public void setProductImg(String productImg)
    {
        this.productImg = productImg;
    }
    
    public String getProductPrice()
    {
        return productPrice;
    }
    
    public void setProductPrice(String productPrice)
    {
        this.productPrice = productPrice;
    }
    
    public Integer getIsHidePrice()
    {
        return isHidePrice;
    }
    
    public void setIsHidePrice(Integer isHidePrice)
    {
        this.isHidePrice = isHidePrice;
    }
    
    public String getCreateTime()
    {
        return createTime;
    }
    
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }
    
    public String getUpdataTime()
    {
        return updataTime;
    }
    
    public void setUpdataTime(String updataTime)
    {
        this.updataTime = updataTime;
    }
    
    public String getProductIntro()
    {
        return productIntro;
    }
    
    public void setProductIntro(String productIntro)
    {
        this.productIntro = productIntro;
    }
    
    public Integer getIsRecommend()
    {
        return isRecommend;
    }
    
    public void setIsRecommend(Integer isRecommend)
    {
        this.isRecommend = isRecommend;
    }
    
    public String getProductOrgPrice()
    {
        return productOrgPrice;
    }
    
    public void setProductOrgPrice(String productOrgPrice)
    {
        this.productOrgPrice = productOrgPrice;
    }
    
    public String getNotice()
    {
        return notice;
    }
    
    public void setNotice(String notice)
    {
        this.notice = notice;
    }
    
    public Integer getIsDelete()
    {
        return isDelete;
    }
    
    public void setIsDelete(Integer isDelete)
    {
        this.isDelete = isDelete;
    }
    
    public String getProductMaterial()
    {
        return productMaterial;
    }
    
    public void setProductMaterial(String productMaterial)
    {
        this.productMaterial = productMaterial;
    }
    
    public String getProductCode()
    {
        return productCode;
    }
    
    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }
    
    public Integer getUserId()
    {
        return userId;
    }
    
    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }
    
    public int getProductId()
    {
        return productId;
    }
    
    public void setProductId(Integer productId)
    {
        this.productId = productId;
    }
    
    public String getClassificationName()
    {
        return classificationName;
    }
    
    public void setClassificationName(String classificationName)
    {
        this.classificationName = classificationName;
    }
    
    public String getBrandCnname()
    {
        return brandCnname;
    }
    
    public void setBrandCnname(String brandCnname)
    {
        this.brandCnname = brandCnname;
    }
    
    public String getBrandName()
    {
        return brandName;
    }
    
    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }
    
    public Integer getClassificationId()
    {
        return classificationId;
    }
    
    public void setClassificationId(Integer classificationId)
    {
        this.classificationId = classificationId;
    }
    
    public String getProductName()
    {
        return productName;
    }
    
    public void setProductName(String productName)
    {
        this.productName = productName;
    }
    
    public Integer getBrandId()
    {
        return brandId;
    }
    
    public void setBrandId(Integer brandId)
    {
        this.brandId = brandId;
    }
    
    public Integer getSystemType()
    {
        return systemType;
    }
    
    public void setSystemType(Integer systemType)
    {
        this.systemType = systemType;
    }
    
    public Integer getSellStatus()
    {
        return sellStatus;
    }
    
    public void setSellStatus(Integer sellStatus)
    {
        this.sellStatus = sellStatus;
    }
}
