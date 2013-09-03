#!/usr/bin/perl -w
use Data::Dumper;
use WebService::Cmis;
use Cache::FileCache;
use Config::Simple;
use XML::LibXML;

our $ALF_XPATH_PROPERTIES = new XML::LibXML::XPathExpression('./*[local-name()="object" and namespace-uri()="'.CMISRA_NS.'"]/*[local-name()="properties" and namespace-uri()="'.'alf'.'"]//*[@propertyDefinitionId]');

my $config = new Config::Simple('../config_params.cfg') or die "aargh $!";
  my $client = WebService::Cmis::getClient(
      url => "https://".$config->param("cmis_repository.ALF_MACHINE_ADDRESS")."/alfresco/cmisatom",
	user => $config->param("cmis_repository.ALF_USERNAME"),
	password => $config->param("cmis_repository.ALF_PASSWORD"),
      cache => new Cache::FileCache({
        cache_root => "/tmp/cmis_client"
      }
    )
  );
  
  my $repo = $client->getRepository;
#  print Dumper($repo);

  my $projectFolder = $config->param("cmis_repository.ROOT_FOLDER");

  my $folder = $repo->getObjectByPath($projectFolder);
#  print Dumper($folder);
  showFolderContents($repo, $folder);

sub showFolderDetails {
  my ($repo, $fold) = @_;
  print "<h2>".$fold->getPath()."</h2>\n";
  my $props = $fold->getProperties;
#  print Dumper($fold->_getDocumentElement->findnodes($ALF_XPATH_PROPERTIES));
# print Dumper($props);
  $rel = $fold->getRelationships;
  while (($entry = $rel->getNext())) {
        $relProps = $entry->getProperties;
        if (defined $relProps->{'cmis:targetId'}) {
            $targetId = $relProps->{'cmis:targetId'}->getValue;
            print $targetId;
            $target = $repo->getObject($targetId);
            #Only works when relationship to a valid CMIS object (folder or doc)
            if (defined $target) {
                print Dumper($target->getProperties);
            }
        }
   }
}

sub showDocumentDetails {
  my $doc = shift;
#  print Dumper($doc);
  my $props = $doc->getProperties;
  if ($props->{'cmis:isLatestVersion'}->getValue eq 1) {
  if (defined $props->{'imap:messageFrom'}) {
	print "<table><tr><th>From</th><th>To</th><th>Subject</th><th>Answered</th></tr>";
	print "<tr><td>";
	print $props->{'imap:messageFrom'}->getValue;
	print "</td><td>";
	print $props->{'imap:messageTo'}->getValue;
	print "</td><td>";
	print $props->{'imap:messageSubject'}->getValue;
	print "</td><td>";
	print $props->{'imap:flagAnswered'}->getValue;
	print "</td></tr></table>";
  } else {
    print $doc->getTitle()."<br/>\n";
  }
# print Dumper($props);
  }
}
sub showFolderContents {
  my ($repo, $fold) = @_;

  showFolderDetails($repo, $fold);
  my $projects = $fold->getChildren();
  while (($entry = $projects->getNext())) {
	if ($entry->isa("WebService::Cmis::Folder")) {
		showFolderContents($repo, $entry);
	} else {
		showDocumentDetails($entry);
	}
  }
}
