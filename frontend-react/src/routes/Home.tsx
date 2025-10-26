import React, { useState } from "react";

// import { Home, Settings } from "tabler-icons-react";

export const Home: React.FC = () => {
  const [expanded, setExpanded] = useState(false);

  const navWidth = expanded ? 200 : 60;

  const menuItems = [
    // { label: "Home", icon: <Home size={24} /> },
    // { label: "Settings", icon: <Settings size={24} /> },
  ];

  return (
    <div>
      <h1>my home</h1>
    </div>
  )
};

export default Home;
