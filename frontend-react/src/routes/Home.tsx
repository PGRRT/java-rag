import ContentWrapper from "@/components/ui/ContentWrapper";
import colorPalette from "@/constants/colorPalette";
import useViewport from "@/hooks/useViewport";
import Chat from "@/layouts/Chat";
import Navbar from "@/layouts/Navbar";
import Sidebar from "@/layouts/Sidebar";

export default function Home() {
  return (
    <ContentWrapper direction="row">
      <Sidebar />
      <ContentWrapper
        padding="1rem 1.2rem"
        flexValue="1 1 auto"
        direction="column"
      >
        <Navbar />
        <Chat />
      </ContentWrapper>
    </ContentWrapper>
  );
}
