function ContentManager() {
  const [content, setContent] = useState([]);

  const handleDelete = (id) => {
    axios.delete(`/api/admin/content/${id}`)
      .then(() => setContent(content.filter(c => c.id !== id)));
  };

  return (
    <Table>
      {content.map(item => (
        <TableRow key={item.id}>
          <TableCell>{item.title}</TableCell>
          <TableCell>
            <Button onClick={() => handleDelete(item.id)}>Delete</Button>
          </TableCell>
        </TableRow>
      ))}
    </Table>
  );
}