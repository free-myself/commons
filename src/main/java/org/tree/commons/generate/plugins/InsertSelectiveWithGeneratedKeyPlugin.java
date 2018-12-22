package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;

import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/22
 */
public class InsertSelectiveWithGeneratedKeyPlugin extends PluginAdapter {
    private String id;

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /****************************** xml *******************************/

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        id = _getPrivateKeyName(introspectedTable);
        if (id != null)
            _addXmlElement(document);
        return true;
    }

    private void _addXmlElement(Document document) {
        List<Element> elements = document.getRootElement().getElements();
        XmlElement element = (XmlElement) document.getRootElement().getElements().get(9);
        XmlElement target = _deal(element);
        if (target == null) {
            for (Element e : elements) {
                target = _deal(e);
                if (target != null)
                    break;
            }
        }
        document.getRootElement().addElement(target);
    }

    private XmlElement _deal(Element element) {
        try {
            XmlElement target = new XmlElement("insert");
            XmlElement element0 = (XmlElement) element;
            Attribute attribute = element0.getAttributes().get(0);
            if ("id".equals(attribute.getName()) && "insertSelective".equals(attribute.getValue())) {
                _dealWithDeepCopy(target, element0);
                return target;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    private void _dealWithDeepCopy(XmlElement dest, XmlElement src) {
        for (Element e : src.getElements())
            dest.addElement(e);

        Attribute attribute = new Attribute("id", "insertSelectiveWithGeneratedKey");
        for (Attribute a : src.getAttributes())
            dest.addAttribute("insertSelective".equals(a.getValue()) ? attribute : a);
        dest.addAttribute(new Attribute("useGeneratedKeys", "true"));
        dest.addAttribute(new Attribute("keyProperty", id));
    }

    private String _getPrivateKeyName(IntrospectedTable table) {
        return table.getPrimaryKeyColumns().size() == 0 ? null : table.getPrimaryKeyColumns().get(0).getJavaProperty();
    }

    /****************************** Mapper *******************************/

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String model = _getModelName(interfaze);
        Method insertSelectiveWithGeneratedKey = new Method("insertSelectiveWithGeneratedKey");
        insertSelectiveWithGeneratedKey.addParameter(new Parameter(new FullyQualifiedJavaType(model), model.toLowerCase()));
        insertSelectiveWithGeneratedKey.setReturnType(new FullyQualifiedJavaType("int"));
        insertSelectiveWithGeneratedKey.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addMethod(insertSelectiveWithGeneratedKey);
        return true;
    }

    private String _getModelName(Interface i) {
        String iName = i.getType().getShortName();
        return iName.substring(0, iName.indexOf("Mapper"));
    }
}