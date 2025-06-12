function Browse() {
  const [trending, setTrending] = useState([]);

  useEffect(() => {
    axios.get('/api/content/trending')
      .then(res => setTrending(res.data));
  }, []);

  return (
    <div className="content-grid">
      {trending.map(content => (
        <ContentCard key={content.id} content={content} />
      ))}
    </div>
  );
}