package jsf_tree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;


public class SimpleTreeBean {
    
    private TreeNode rootNode = null;
    private List<String> selectedNodeChildren = new ArrayList<String>();    
    
    private String nodeTitle;
    private static final String DATA_PATH = "/data.properties";
    
    private void addNodes(String path, TreeNode node, Properties properties) {
        boolean end = false;
        int counter = 1;
        
        while (!end) {
            String key = path != null ? path + '.' + counter : String.valueOf(counter);
            
            String value = properties.getProperty(key);
            if (value != null) {
                TreeNodeImpl nodeImpl = new TreeNodeImpl();
                nodeImpl.setData(value);
                node.addChild(new Integer(counter), nodeImpl);
                addNodes(key, nodeImpl, properties);
                counter++;
            } else {
                end = true;
            }
        }
    }
    
    private void loadTree() {
    	
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        InputStream dataStream = externalContext.getResourceAsStream(DATA_PATH);
        try {
            Properties properties = new Properties();
            properties.load(dataStream);
            
            rootNode = new TreeNodeImpl();
            addNodes(null, rootNode, properties);
            
        } catch (IOException e) {
        	e.printStackTrace();
            throw new FacesException(e.getMessage(), e);
            
        } finally {
            if (dataStream != null) {
                try {
                    dataStream.close();
                } catch (IOException e) {
                    externalContext.log(e.getMessage(), e);
                }
            }
        }
    }
    
    public void processSelection(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        nodeTitle = (String) tree.getRowData();
        selectedNodeChildren.clear();
        TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
        if (currentNode.isLeaf()){
            selectedNodeChildren.add((String)currentNode.getData());
        }else
        {
            Iterator<Map.Entry<Object, TreeNode>> it = currentNode.getChildren();
            while (it!=null &&it.hasNext()) {
                Map.Entry<Object, TreeNode> entry = it.next();
                selectedNodeChildren.add(entry.getValue().getData().toString()); 
            }
        }
    }
    
    public TreeNode getTreeNode() {
        if (rootNode == null) {
            loadTree();
        }
        
        return rootNode;
    }


    
    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    
}
