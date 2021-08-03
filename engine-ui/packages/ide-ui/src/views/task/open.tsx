// TODO, refactor
import React from "react";
import { Button, Form, Input, Select } from "antd";
import { WrappedFormUtils } from "antd/lib/form/Form";
import FormItem from "antd/lib/form/FormItem";

const Option = Select.Option;

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};
const tailFormItemLayout = {
  wrapperCol: {
    xs: {
      span: 24,
      offset: 0,
    },
    sm: {
      span: 16,
      offset: 8,
    },
  },
};

interface OpenProps {
  currentId: number;
  onSubmit?: (values: any) => void;
  form: WrappedFormUtils<any>;
}

const taskType = [
  {
    value: "SparkSql",
    text: "SparkSql",
  },
  {
    value: "DataSync",
    text: "数据同步",
  }
];

class Open extends React.PureComponent<OpenProps, {}> {
  handleSubmit = (e: any) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.props.onSubmit?.(values);
      }
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...formItemLayout} label="任务名称">
          {getFieldDecorator("name", {
            rules: [
              {
                max: 64,
                message: "任务名称不得超过20个字符！",
              },
              {
                required: true,
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem {...formItemLayout} label="任务类型">
          {getFieldDecorator("taskType", {
            rules: [
              {
                required: true,
              },
            ],
          })(
            <Select>
              {taskType.map((type) => (
                <Option key={type.value} value={type.value}>
                  {type.text}
                </Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="描述" hasFeedback>
          {getFieldDecorator("taskDesc", {
            rules: [
              {
                max: 200,
                message: "描述请控制在200个字符以内！",
              },
            ],
          })(<Input.TextArea disabled={false} rows={4} />)}
        </FormItem>
        <FormItem {...tailFormItemLayout}>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </FormItem>
      </Form>
    );
  }
}

export default Form.create<OpenProps>({ name: "open" })(Open);
