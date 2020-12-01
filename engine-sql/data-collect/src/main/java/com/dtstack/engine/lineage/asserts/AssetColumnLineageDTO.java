package com.dtstack.engine.lineage.asserts;

/**
 * @author chener
 * @Classname AssetColumnLineageDTO
 * @Description TODO
 * @Date 2020/12/1 14:58
 * @Created chener@dtstack.com
 */
public class AssetColumnLineageDTO {

   private Integer isManual;

   private Long lineageColumnId;

   private Long inputColumnId;

   public Integer getIsManual() {
      return isManual;
   }

   public void setIsManual(Integer isManual) {
      this.isManual = isManual;
   }

   public Long getLineageColumnId() {
      return lineageColumnId;
   }

   public void setLineageColumnId(Long lineageColumnId) {
      this.lineageColumnId = lineageColumnId;
   }

   public Long getInputColumnId() {
      return inputColumnId;
   }

   public void setInputColumnId(Long inputColumnId) {
      this.inputColumnId = inputColumnId;
   }
}
