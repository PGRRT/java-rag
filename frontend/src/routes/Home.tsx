import ContentWrapper from "@/components/ui/ContentWrapper";
import colorPalette from "@/constants/colorPalette";
import useViewport from "@/hooks/useViewport";
import Chat from "@/layouts/Chat";
import Navbar from "@/layouts/Navbar";
import Sidebar from "@/layouts/Sidebar";

export default function Home() {
  return (
    <ContentWrapper direction="row" minHeight="100vh">
      <Sidebar />
      <ContentWrapper width="100%" direction="column">
        <Navbar />
        <Chat />
      </ContentWrapper>
    </ContentWrapper>
  );
}
