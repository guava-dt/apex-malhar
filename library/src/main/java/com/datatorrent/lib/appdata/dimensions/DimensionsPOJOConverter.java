/*
 * Copyright (c) 2015 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatorrent.lib.appdata.dimensions;

import com.datatorrent.lib.appbuilder.convert.pojo.PojoFieldRetriever;
import com.datatorrent.lib.appdata.gpo.GPOMutable;
import com.datatorrent.lib.converter.Converter;
import com.google.common.base.Preconditions;
import javax.validation.constraints.NotNull;

import java.util.List;

public class DimensionsPOJOConverter implements Converter<Object, AggregateEvent, DimensionsConversionContext>
{
  @NotNull
  private PojoFieldRetriever pojoFieldRetriever;

  public DimensionsPOJOConverter()
  {
  }

  @Override
  public AggregateEvent convert(Object inputEvent, DimensionsConversionContext context)
  {
    GPOMutable key = new GPOMutable(context.keyFieldsDescriptor);

    List<String> fields = key.getFieldDescriptor().getFields().getFieldsList();

    for(int fieldIndex = 0;
        fieldIndex < fields.size();
        fieldIndex++) {
      String field = fields.get(fieldIndex);
      if(field.equals(DimensionsDescriptor.DIMENSION_TIME_BUCKET)) {
      }
      else if(field.equals(DimensionsDescriptor.DIMENSION_TIME)) {
        long timestamp = pojoFieldRetriever.getLong(field, inputEvent);
        context.dd.getTimeBucket().roundDown(timestamp);
        key.setField(field, timestamp);
      }
      else {
        key.setField(field, pojoFieldRetriever.getLong(field, inputEvent));
      }
    }

    GPOMutable aggregates = new GPOMutable(context.aggregateDescriptor);

    fields = aggregates.getFieldDescriptor().getFields().getFieldsList();

    for(int fieldIndex = 0;
        fieldIndex < fields.size();
        fieldIndex++) {
      String field = fields.get(fieldIndex);
      aggregates.setField(field, pojoFieldRetriever.getLong(field, inputEvent));
    }

    return new AggregateEvent(new GPOMutable(key),
                              aggregates,
                              context.schemaID,
                              context.dimensionDescriptorID,
                              context.aggregatorID);
  }

  /**
   * @return the pojoFieldRetriever
   */
  public PojoFieldRetriever getPojoFieldRetriever()
  {
    return pojoFieldRetriever;
  }

  /**
   * @param pojoFieldRetriever the pojoFieldRetriever to set
   */
  public void setPojoFieldRetriever(@NotNull PojoFieldRetriever pojoFieldRetriever)
  {
    this.pojoFieldRetriever = Preconditions.checkNotNull(pojoFieldRetriever);
  }
}
